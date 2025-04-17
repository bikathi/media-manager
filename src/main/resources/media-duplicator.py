import shutil
import os
import argparse
import sys

def duplicate_folder(original_folder, new_folder_name):
    parent_directory = os.path.dirname(original_folder)
    new_folder_path = os.path.join(parent_directory, new_folder_name)

    try:
        shutil.copytree(original_folder, new_folder_path)
        print(f"Successfully duplicated '{original_folder}' as '{new_folder_path}'")
    except FileExistsError:
        print(f"Error: Folder '{new_folder_name}' already exists.")
        return False
    except Exception as e:
        print(f"An error occurred: {e}")
        return False
    return True

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Duplicate a folder and assign a new name to the copy.")
    parser.add_argument("--folder-path", required=True, help="Path to the original folder")
    parser.add_argument("--folder-name", required=True, help="New name for the duplicated folder")

    args = parser.parse_args()

    success = duplicate_folder(args.folder_path, args.folder_name)
    if not success:
        sys.exit(1)