import os
import subprocess
from PIL import Image, ImageDraw, ImageFont
import argparse

def watermark_webp(input_path, output_path, watermark_text="Watermark"):
    try:
        image = Image.open(input_path).convert("RGBA")
        draw = ImageDraw.Draw(image)
        font_size = 50
        try:
            font = ImageFont.truetype("Ubuntu-Medium.ttf", font_size)
        except IOError:
            font = ImageFont.load_default()

        try:
            text_bbox = draw.textbbox((0, 0), watermark_text, font=font)
            text_width = text_bbox[2] - text_bbox[0]
            text_height = text_bbox[3] - text_bbox[1]
        except AttributeError:
            text_width, text_height = draw.textsize(watermark_text, font)

        watermark_sections = watermark_text.split("#")
        y = 10
        for section in watermark_sections:
            draw.text((10, y), section, font=font, fill=(255, 68, 51, 256))
            y += font_size + 10
        image.save(output_path, "webp")
        return True
    except Exception as e:
        print(f"Error adding watermark to {input_path}: {e}")
        return False

def compress_webp(input_path, output_path, quality=80, method=6):
    try:
        subprocess.run(
            [
                "cwebp",
                "-q", str(quality),
                "-m", str(method),
                "-sharp_yuv",
                "-strong",
                "-alpha_q", "90",
                "-metadata", "none",
                "-mt",
                "-f", "50",
                "-pass", "5",
                input_path,
                "-o", output_path,
            ],
            check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL,
        )
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error compressing {input_path}: {e}")
        return False
    except FileNotFoundError:
        print("cwebp not found. Make sure it's installed and in your PATH.")
        return False

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Compress and watermark WebP files')
    parser.add_argument('input_file', help='Path to the input WebP file')
    parser.add_argument('output_file', help='Path to the output WebP file')
    parser.add_argument('--watermark_text', help='Text to add as watermark', default='Watermark')
    parser.add_argument('--skip_compression', action='store_true', help='Skip compression step')
    args = parser.parse_args()

    temp_path = os.path.splitext(args.input_file)[0] + "_temp.webp"

    if not os.path.exists(args.input_file):
        print(f"Input file {args.input_file} does not exist.")
        exit(1)

    if args.skip_compression:
        temp_path = args.input_file
    else:
        if not compress_webp(args.input_file, temp_path):
            print(f"An error occurred while compressing {args.input_file}")
            exit(1)

    if not watermark_webp(temp_path, args.output_file, args.watermark_text):
        print(f"An error occurred while adding watermark to {temp_path}")
        exit(1)

    if not args.skip_compression:
        os.remove(temp_path)
