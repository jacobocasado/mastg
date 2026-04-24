e asm.bytes=false
e scr.color=false
e asm.var=false

?e List all references to "UITextField":
f~UITextField

?e
?e List all references to the "setSecureTextEntry:" selector:
f~setSecureTextEntry

?e
?e Cross-references to the "setSecureTextEntry:" selector:
axt @ 0x100010138

?e
?e Disassembly around the password field setup:
pd--10 @ 0x1000045f0
