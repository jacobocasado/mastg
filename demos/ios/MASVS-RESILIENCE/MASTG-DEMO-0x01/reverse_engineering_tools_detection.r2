e asm.bytes=false
e scr.color=false
e asm.var=false

?e

?e search for Frida artifact strings:

/ FridaGadget
/ frida-agent
/ cynject
/ libcycript

?e

?e search for dyld image iteration APIs:

/ _dyld_image_count
/ _dyld_get_image_name

?e

?e search for port 27042 (frida-server default port):

/ 27042

?e

?e Searching for ReverseEngineeringToolsDetector strings:

iz~+[Rr]everse

?e

?e xrefs to ReverseEngineeringToolsDetector strings:

axt @ hit0_0

?e

?e Disassembled detection function:

pdf @ hit0_0
