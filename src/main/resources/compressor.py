import os
import subprocess
from PIL import Image, ImageDraw, ImageFont
import argparse

def compress_and_watermark_webp(input_path, output_path, quality=80, method=6, watermark_text="Watermark"):
    """Adds a watermark to a WebP image and then compresses it."""
    temp_watermarked_path = os.path.splitext(input_path)[0] + "_temp.webp"

    # Compression
    try:
        subprocess.run(
            [
                "cwebp",
                "-q", str(quality),
                "-m", str(method),
                "-sharp_yuv",
                "-strong",
                "-alpha_q", "90",  # Adjusted alpha quality
                "-metadata", "none",  # Strip metadata
                "-mt",  # Enable multi-threading
                "-f", "50",  # Add filtering
                "-pass", "5",  # Set number of passes
                input_path,
                "-o", temp_watermarked_path,
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
        image = Image.open(temp_watermarked_path).convert("RGBA")
        draw = ImageDraw.Draw(image)

        # Load font and set font size
        font_size = 50  # Adjust font size as needed
        try:
            font = ImageFont.truetype("Ubuntu-Medium.ttf", font_size)  # Use a font installed on your system
        except IOError:
            font = ImageFont.load_default()  # Fallback if the font is not found

        try:
            # Pillow 10.0.0 and later
            text_bbox = draw.textbbox((0, 0), watermark_text, font=font)
            text_width = text_bbox[2] - text_bbox[0]
            text_height = text_bbox[3] - text_bbox[1]
        except AttributeError:
            # Older Pillow versions
            text_width, text_height = draw.textsize(watermark_text, font)

        # Split watermark text into sections
        watermark_sections = watermark_text.split("#")

        # Render each section of the watermark
        y = 10  # Initial y-coordinate
        for section in watermark_sections:
            draw.text((10, y), section, font=font, fill=(255, 255, 0, 256))  # Render text in white
            y += font_size + 10  # Move to next line

        image.save(output_path, "webp")
        os.remove(temp_watermarked_path)
        return True
    except Exception as e:
        print(f"Error adding watermark to {temp_watermarked_path}: {e}")
        return False

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Compress and watermark WebP files')
    parser.add_argument('input_file', help='Path to the input WebP file')
    parser.add_argument('output_file', help='Path to the output WebP file')
    parser.add_argument('--watermark_text', help='Text to add as watermark', default='')
    args = parser.parse_args()

    compression_quality = 50
    compression_method = 6
    watermark_text = ""

    print(f"Input file: {args.input_file} Output file: {args.output_file} Watermark text: {args.watermark_text}")

    if not os.path.exists(args.input_file):
        print(f"Input file {args.input_file} does not exist.")
        exit(1)

    if args.watermark_text:
        watermark_text = args.watermark_text
    else:
        watermark_text = ""

    if compress_and_watermark_webp(args.input_file, args.output_file, compression_quality, compression_method, watermark_text):
        print(f"Successfully processed {args.input_file}. The output file is {args.output_file}")
    else:
        print(f"An error occurred while processing {args.input_file}")