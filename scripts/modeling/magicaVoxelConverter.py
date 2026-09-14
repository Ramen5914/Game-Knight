import argparse
import json
from dataclasses import dataclass
from pathlib import Path
from typing import BinaryIO

@dataclass
class VoxelModel:
  size: tuple[int, int, int]
  voxels: list[tuple[int, int, int, int]]

  def to_3d_array(self) -> list[list[list[bool]]]:
    size_x, size_y, size_z = self.size
    array_3d = [
      [[False for _ in range(size_x)] for _ in range(size_y)]
      for _ in range(size_z)
    ]
    for x, y, z, _color_index in self.voxels:
      if 0 <= x < size_x and 0 <= y < size_y and 0 <= z < size_z:
        array_3d[z][y][x] = True
    return array_3d

  def to_greedy_prisms(self) -> list[dict]:
    occupancy = self.to_3d_array()
    size_x, size_y, size_z = self.size
    visited = [
      [[False for _ in range(size_x)] for _ in range(size_y)]
      for _ in range(size_z)
    ]
    prisms: list[dict] = []

    for z in range(size_z):
      for y in range(size_y):
        for x in range(size_x):
          if not occupancy[z][y][x] or visited[z][y][x]:
            continue

          max_x = x
          while (
            max_x + 1 < size_x
            and occupancy[z][y][max_x + 1]
            and not visited[z][y][max_x + 1]
          ):
            max_x += 1

          max_y = y
          can_expand_y = True
          while can_expand_y and max_y + 1 < size_y:
            next_y = max_y + 1
            for xx in range(x, max_x + 1):
              if not occupancy[z][next_y][xx] or visited[z][next_y][xx]:
                can_expand_y = False
                break
            if can_expand_y:
              max_y = next_y

          max_z = z
          can_expand_z = True
          while can_expand_z and max_z + 1 < size_z:
            next_z = max_z + 1
            for yy in range(y, max_y + 1):
              for xx in range(x, max_x + 1):
                if not occupancy[next_z][yy][xx] or visited[next_z][yy][xx]:
                  can_expand_z = False
                  break
              if not can_expand_z:
                break
            if can_expand_z:
              max_z = next_z

          for zz in range(z, max_z + 1):
            for yy in range(y, max_y + 1):
              for xx in range(x, max_x + 1):
                visited[zz][yy][xx] = True

          prisms.append(
            {
              "origin": [x, y, z],
              "size": [max_x - x + 1, max_y - y + 1, max_z - z + 1],
            }
          )

    return prisms


@dataclass
class VoxFile:
  version: int
  models: list[VoxelModel]


def read_u32(file: BinaryIO) -> int:
  data = file.read(4)
  if len(data) != 4:
    raise ValueError("Unexpected end of file while reading uint32")
  return int.from_bytes(data, byteorder="little", signed=False)


def read_chunk_id(file: BinaryIO) -> str:
  data = file.read(4)
  if len(data) != 4:
    raise ValueError("Unexpected end of file while reading chunk id")
  return data.decode("ascii")


def parse_vox(path: Path) -> VoxFile:
  with path.open("rb") as file:
    header = read_chunk_id(file)
    if header != "VOX ":
      raise ValueError(f"Invalid VOX header: {header!r}")

    version = read_u32(file)

    main_chunk_id = read_chunk_id(file)
    if main_chunk_id != "MAIN":
      raise ValueError(f"Expected MAIN chunk, got {main_chunk_id!r}")

    main_content_bytes = read_u32(file)
    main_children_bytes = read_u32(file)

    if main_content_bytes:
      file.seek(main_content_bytes, 1)

    children_end = file.tell() + main_children_bytes

    models: list[VoxelModel] = []
    expected_models: int | None = None
    current_size: tuple[int, int, int] | None = None

    while file.tell() < children_end:
      chunk_id = read_chunk_id(file)
      content_bytes = read_u32(file)
      children_bytes = read_u32(file)
      content_start = file.tell()

      if chunk_id == "PACK":
        expected_models = read_u32(file)
      elif chunk_id == "SIZE":
        size_x = read_u32(file)
        size_y = read_u32(file)
        size_z = read_u32(file)
        current_size = (size_x, size_y, size_z)
      elif chunk_id == "XYZI":
        if current_size is None:
          raise ValueError("Found XYZI chunk before SIZE chunk")
        voxel_count = read_u32(file)
        voxels: list[tuple[int, int, int, int]] = []
        for _ in range(voxel_count):
          voxel = file.read(4)
          if len(voxel) != 4:
            raise ValueError("Unexpected end of file while reading XYZI voxel")
          voxels.append((voxel[0], voxel[1], voxel[2], voxel[3]))
        models.append(VoxelModel(size=current_size, voxels=voxels))
        current_size = None

      file.seek(content_start + content_bytes)
      if children_bytes:
        file.seek(children_bytes, 1)

    if expected_models is not None and expected_models != len(models):
      raise ValueError(
        f"PACK declared {expected_models} model(s), parsed {len(models)}"
      )

    return VoxFile(version=version, models=models)


def model_to_json(model: VoxelModel, include_greedy_mesh: bool = False) -> dict:
  payload = {
    "size": list(model.size),
    "array": model.to_3d_array(),
  }
  if include_greedy_mesh:
    prisms = model.to_greedy_prisms()
    payload["prisms"] = prisms
    payload["prism_count"] = len(prisms)
    payload["voxel_count"] = len(model.voxels)
  return payload


def main() -> None:
  parser = argparse.ArgumentParser(
    description="Parse a MagicaVoxel .vox file into 3D array data"
  )
  parser.add_argument("input", nargs="?", default="knight.vox", help="Path to .vox file")
  parser.add_argument(
    "--model",
    type=int,
    default=None,
    help="Model index to export (default: export all models)",
  )
  parser.add_argument(
    "--output",
    default=None,
    help="Optional output JSON file path (prints to stdout if omitted)",
  )
  parser.add_argument(
    "--pretty",
    action="store_true",
    help="Pretty-print JSON output",
  )
  parser.add_argument(
    "--greedy-mesh",
    action="store_true",
    help="Include greedy-meshed rectangular prisms in output",
  )
  args = parser.parse_args()

  vox_file = parse_vox(Path(args.input))

  if args.model is None:
    payload = {
      "version": vox_file.version,
      "model_count": len(vox_file.models),
      "models": [
        model_to_json(model, include_greedy_mesh=args.greedy_mesh)
        for model in vox_file.models
      ],
    }
  else:
    if args.model < 0 or args.model >= len(vox_file.models):
      raise IndexError(
        f"Model index out of range: {args.model} (available: 0..{len(vox_file.models) - 1})"
      )
    model = vox_file.models[args.model]
    payload = {
      "version": vox_file.version,
      "model_index": args.model,
      "model": model_to_json(model, include_greedy_mesh=args.greedy_mesh),
    }

  indent = 2 if args.pretty else None
  output_text = json.dumps(payload, indent=indent)

  if args.output:
    Path(args.output).write_text(output_text, encoding="utf-8")
    print(f"Wrote voxel array JSON to {args.output}")
  else:
    print(output_text)


if __name__ == "__main__":
  main()