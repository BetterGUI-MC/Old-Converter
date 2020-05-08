# Converter
Converter from other plugins to BetterGUI

## Usage
> `java -Dtype=<type> -jar Converter.jar <filename> [<filename1> <filename2> ...]`
* Available types:
  * `CC`: Chest Commands GUI
  * `DM`: Deluxe Menus
* For non-English characters, add `-Dfile.encoding="UTF-8"` before `-jar` in your command
  * `java -Dtype=<type> -Dfile.encoding="UTF-8 -jar Converter.jar <filename> [<filename1> <filename2> ...]`

## Note
This is not a perfect converter, it can only convert some basic parts of the old config.
You may need to check it yourself
