
search for Frida artifact strings:
0x100130a20 hit0_0 "FridaGadget"
0x100130a30 hit1_0 "frida-agent"
0x100130a40 hit2_0 "cynject"
0x100130a50 hit3_0 "libcycript"

search for dyld image iteration APIs:
0x1000080a8 hit4_0 "__dyld_image_count"
0x1000080b8 hit5_0 "__dyld_get_image_name"

search for port 27042 (frida-server default port):
0x100130b20 hit6_0 "27042"

Searching for ReverseEngineeringToolsDetector strings:
7    0x00130a20  0x100130a20 11  12   4.__TEXT.__cstring         ascii   FridaGadget
8    0x00130a30  0x100130a30 11  12   4.__TEXT.__cstring         ascii   frida-agent
9    0x00130a40  0x100130a40 7   8    4.__TEXT.__cstring         ascii   cynject
10   0x00130a50  0x100130a50 10  11   4.__TEXT.__cstring         ascii   libcycript
11   0x00130a60  0x100130a60 30  31   4.__TEXT.__cstring         ascii   _TtC10MASTestApp30ReverseEngineeringToolsDetector
23   0x00130b00  0x100130b00 50  51   4.__TEXT.__cstring         ascii   Reverse Engineering Tools: Detected ⚠️
24   0x00130b40  0x100130b40 55  56   4.__TEXT.__cstring         ascii   Reverse Engineering Tools: Not Detected ✅

xrefs to ReverseEngineeringToolsDetector strings:
sym.func.100007a10 0x100007b44 [STRN:-w-] add x9, x9, str.FridaGadget

Disassembled detection function:
            ; CALL XREF from sym.func.10000aef8 @ 0x10000af28(x)
┌ 412: sym.func.100007a10 (int64_t arg_0h, int64_t arg_50h, void *arg0);
│           0x100007a10      sub sp, sp, 0x50
│           0x100007a14      stp x22, x21, [var_10h]
│           0x100007a18      stp x20, x19, [var_20h]
│           0x100007a1c      stp x29, x30, [var_30h]
│           0x100007a20      add x29, var_30h
│           0x100007a24      bl sym.imp._dyld_image_count              ; uint32_t _dyld_image_count(void)
│           0x100007a28      mov x19, x0
│           0x100007a2c      cbz x0, 0x100007b80
│           0x100007a30      movz x20, 0
│           ; loop over loaded images
│       ┌─> 0x100007a34      mov x0, x20                              ; uint32_t image_index
│       │   0x100007a38      bl sym.imp._dyld_get_image_name          ; const char *_dyld_get_image_name(uint32_t image_index)
│       │   0x100007a3c      cbz x0, 0x100007b78
│       │   0x100007a40      bl sym.imp.strlen
│       │   0x100007a44      mov x2, x0
│       │   0x100007a48      adrp x8, sym.imp.__error                 ; 0x100130000
│       │   0x100007a4c      add x8, x8, 0xa20                        ; 0x100130a20 ; "FridaGadget"
│       │   0x100007a50      bl sym.imp.strcasestr
│       │   0x100007a54      cbz x0, 0x100007b60
│       │   0x100007a58      adrp x8, sym.imp.__error                 ; 0x100130000
│       │   0x100007a5c      add x8, x8, 0xa60                        ; "Detected reverse engineering tool library: "
│       │   0x100007a60      bl sym.imp.objc_msgSend
│       │   0x100007a64      mov x29, x29
│       │   0x100007a68      bl sym.imp.objc_retainAutoreleasedReturnValue
│       │   ; ... (frida-agent, cynject, libcycript checks follow the same pattern)
│       │   0x100007b60      add x20, x20, 1
│       │   0x100007b64      cmp x20, x19
│       └─< 0x100007b68      b.lo 0x100007a34
│           ; port 27042 check
│           0x100007b70      bl sym.func.100007c10                    ; isFridaServerRunning(host:port:)
│           0x100007b74      tbz w0, 0, 0x100007b90
│           0x100007b78      adrp x8, sym.imp.__error                 ; 0x100130000
│           0x100007b7c      add x8, x8, 0xb20                        ; 0x100130b20 ; "Detected frida-server listening on port 27042"
│           0x100007b80      bl sym.imp.objc_msgSend
│           0x100007b90      ldp x29, x30, [var_30h]
│           0x100007b94      ldp x20, x19, [var_20h]
│           0x100007b98      ldp x22, x21, [var_10h]
│           0x100007b9c      add sp, arg_50h
└           0x100007ba0      ret
