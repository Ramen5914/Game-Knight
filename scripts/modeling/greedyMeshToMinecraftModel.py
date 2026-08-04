import argparse
import json
from pathlib import Path
from typing import Any

def clean_number(value: float) -> int | float:
  rounded = round(value, 4)
  if float(rounded).is_integer():
    return int(rounded)
  return rounded


def rect_uv(width: float, height: float) -> list[int | float]:
  return [0, 0, clean_number(width), clean_number(height)]


def load_model_section(payload: dict[str, Any], model_index: int | None) -> dict[str, Any]:
  if "model" in payload:
    return payload["model"]

  if "models" in payload:
    models = payload["models"]
    if not isinstance(models, list) or not models:
      raise ValueError("Input JSON contains 'models' but it is empty or invalid")

    selected_index = 0 if model_index is None else model_index
    if selected_index < 0 or selected_index >= len(models):
      raise IndexError(
        f"Model index out of range: {selected_index} (available: 0..{len(models) - 1})"
      )
    return models[selected_index]

  raise ValueError("Input JSON must contain either 'model' or 'models'")


def build_elements(
  model: dict[str, Any],
  fit_size: float,
  scale_override: float | None,
) -> list[dict[str, Any]]:
  if "size" not in model or "prisms" not in model:
    raise ValueError("Model JSON must include 'size' and 'prisms' fields")

  size_x, size_y, size_z = model["size"]
  raw_x = float(size_x)
  raw_y = float(size_z)
  raw_z = float(size_y)

  if scale_override is not None:
    scale = scale_override
  else:
    max_horizontal = max(raw_x, raw_z)
    divisor = 1
    while max_horizontal / divisor > fit_size:
      divisor *= 2
    scale = 1.0 / divisor

  offset_x = (fit_size - raw_x * scale) / 2.0
  offset_y = 0.0
  offset_z = (fit_size - raw_z * scale) / 2.0

  elements: list[dict[str, Any]] = []
  prisms = model["prisms"]
  for index, prism in enumerate(prisms):
    origin_x, origin_y, origin_z = prism["origin"]
    prism_x, prism_y, prism_z = prism["size"]

    from_x = origin_x * scale + offset_x
    from_y = origin_z * scale + offset_y
    from_z = origin_y * scale + offset_z

    to_x = (origin_x + prism_x) * scale + offset_x
    to_y = (origin_z + prism_z) * scale + offset_y
    to_z = (origin_y + prism_y) * scale + offset_z

    width_x = to_x - from_x
    height_y = to_y - from_y
    depth_z = to_z - from_z

    element = {
      "name": str(index),
      "from": [clean_number(from_x), clean_number(from_y), clean_number(from_z)],
      "to": [clean_number(to_x), clean_number(to_y), clean_number(to_z)],
      "rotation": {
        "angle": 0,
        "axis": "y",
        "origin": [clean_number(from_x), clean_number(from_y), clean_number(from_z)],
      },
      "color": index % 9,
      "faces": {
        "north": {"uv": rect_uv(width_x, height_y), "texture": "#missing"},
        "east": {"uv": rect_uv(depth_z, height_y), "texture": "#missing"},
        "south": {"uv": rect_uv(width_x, height_y), "texture": "#missing"},
        "west": {"uv": rect_uv(depth_z, height_y), "texture": "#missing"},
        "up": {"uv": rect_uv(width_x, depth_z), "texture": "#missing"},
        "down": {"uv": rect_uv(width_x, depth_z), "texture": "#missing"},
      },
    }
    elements.append(element)

  return elements


def main() -> None:
  parser = argparse.ArgumentParser(
    description="Convert greedy mesh prism JSON into a Minecraft model JSON"
  )
  parser.add_argument("input", help="Path to greedy mesh JSON file")
  parser.add_argument(
    "--output",
    default=None,
    help="Output Minecraft model JSON path (default: <input>_minecraft.json)",
  )
  parser.add_argument(
    "--model",
    type=int,
    default=None,
    help="Model index when input contains a 'models' array (default: 0)",
  )
  parser.add_argument(
    "--fit-size",
    type=float,
    default=16.0,
    help="Target size for auto-fit in Minecraft units (default: 16)",
  )
  parser.add_argument(
    "--scale",
    type=float,
    default=None,
    help="Override auto-fit scale with an explicit unit scale",
  )
  parser.add_argument("--pretty", action="store_true", help="Pretty-print output JSON")
  args = parser.parse_args()

  input_path = Path(args.input)
  payload = json.loads(input_path.read_text(encoding="utf-8"))
  model = load_model_section(payload, args.model)

  elements = build_elements(model, fit_size=args.fit_size, scale_override=args.scale)

  output_payload = {
    "format_version": "1.9.0",
    "credit": "Generated from greedy mesh output",
    "elements": elements,
  }

  output_path = (
    Path(args.output)
    if args.output
    else input_path.with_name(f"{input_path.stem}_minecraft.json")
  )
  indent = 2 if args.pretty else None
  output_path.write_text(json.dumps(output_payload, indent=indent), encoding="utf-8")
  print(f"Wrote Minecraft model JSON to {output_path}")


if __name__ == "__main__":
  main()