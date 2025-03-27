import os
import subprocess
from PIL import Image, ImageDraw, ImageFont
import argparse

def compress_and_watermark_webp(input_path, output_path, quality=80, method=6, watermark_text="Watermark"):
    """Compresses and adds a watermark to a WebP image."""
    temp_compressed_path = os.path.splitext(output_path)[0] + "_temp.webp"

    # Compression
    try:
        subprocess.run(
            [
                "cwebp",
                "-q", str(quality),
                "-m", str(method),
                input_path,
                "-o", temp_compressed_path,
            ],
            check=True,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
    except subprocess.CalledProcessError as e:
        print(f"Error compressing {input_path}: {e}")
        return False
    except FileNotFoundError:
        print("cwebp not found. Make sure it's installed and in your PATH.")
        return False

    # Watermarking
    try:
        image = Image.open(temp_compressed_path).convert("RGBA")
        draw = ImageDraw.Draw(image)
        font = ImageFont.load_default()
        try:
            # Pillow 10.0.0 and later
            text_bbox = draw.textbbox((0, 0), watermark_text, font=font)
            text_width = text_bbox[2] - text_bbox[0]
            text_height = text_bbox[3] - text_bbox[1]
        except AttributeError:
            # Older Pillow versions
            text_width, text_height = draw.textsize(watermark_text, font)

        text_x = 10
        text_y = 10
        draw.text((text_x, text_y), watermark_text, font=font, fill=(255, 255, 0, 128))
        image.save(output_path, "webp")
        os.remove(temp_compressed_path)
        return True

    except Exception as e:
        print(f"Error adding watermark to {input_path}: {e}")
        if os.path.exists(temp_compressed_path):
            os.remove(temp_compressed_path)
        return False

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Compress and watermark WebP files')
    parser.add_argument('input_file', help='Path to the input WebP file')
    parser.add_argument('output_file', help='Path to the output WebP file')
    parser.add_argument('watermark_text', help='Text to add as watermark', default='')
    args = parser.parse_args()

    compression_quality = 75
    compression_method = 6
    watermark_text = "My Custom Watermark 123"

    print(f"Input file: {args.input_file} Output file: {args.output_file} Watermark text: {args.watermark_text}")

    if not os.path.exists(args.input_file):
        print(f"Input file {args.input_file} does not exist.")
        exit(1)

    if compress_and_watermark_webp(args.input_file, args.output_file, compression_quality, compression_method, watermark_text):
        print(f"Successfully processed {args.input_file}. The output file is {args.output_file}")
    else:
        print(f"An error occurred while processing {args.input_file}")