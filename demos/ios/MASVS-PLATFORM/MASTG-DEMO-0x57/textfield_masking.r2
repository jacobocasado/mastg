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
?e (use the address from reloc.fixup.setSecureTextEntry: in the f~setSecureTextEntry output above)
axt @ 0x00017474

?e
?e Disassembly around the password field setup:
?e (use the caller address from the axt output above)
pd--10 @ 0x48fc
