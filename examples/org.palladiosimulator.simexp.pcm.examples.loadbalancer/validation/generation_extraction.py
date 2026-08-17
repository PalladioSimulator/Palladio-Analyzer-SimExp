import sys
import re

def filter_gen(filename):
    pattern = re.compile(r"(.*)fitness status in generation (.*) for:\s(.*)")

    try:
        with open(filename, 'r', encoding="utf-8") as file:
            for line in file:
                currentMatch = pattern.match(line)
                if currentMatch:
                    print(currentMatch.group(2) + ": " + currentMatch.group(3))
    except FileNotFoundError:
        print(f"Error: File '{filename}' not found.")
    except Exception as e:
        print(f"An error occurred: {e}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python filter_generations.py <filename>")
    else:
        filter_gen(sys.argv[1])
        

