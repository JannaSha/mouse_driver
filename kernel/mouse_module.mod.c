#include <linux/module.h>
#include <linux/vermagic.h>
#include <linux/compiler.h>

MODULE_INFO(vermagic, VERMAGIC_STRING);

__visible struct module __this_module
__attribute__((section(".gnu.linkonce.this_module"))) = {
	.name = KBUILD_MODNAME,
	.init = init_module,
#ifdef CONFIG_MODULE_UNLOAD
	.exit = cleanup_module,
#endif
	.arch = MODULE_ARCH_INIT,
};

static const struct modversion_info ____versions[]
__used
__attribute__((section("__versions"))) = {
	{ 0xc6c01fa, __VMLINUX_SYMBOL_STR(module_layout) },
	{ 0x24a1ccac, __VMLINUX_SYMBOL_STR(platform_device_unregister) },
	{ 0x9bc9fbf7, __VMLINUX_SYMBOL_STR(sysfs_remove_group) },
	{ 0xf61c1db4, __VMLINUX_SYMBOL_STR(input_free_device) },
	{ 0x99fe2258, __VMLINUX_SYMBOL_STR(input_unregister_device) },
	{ 0xecdce1b, __VMLINUX_SYMBOL_STR(input_register_device) },
	{ 0x729a53c5, __VMLINUX_SYMBOL_STR(input_allocate_device) },
	{ 0xf0ea8efd, __VMLINUX_SYMBOL_STR(sysfs_create_group) },
	{ 0xc5a852b8, __VMLINUX_SYMBOL_STR(platform_device_register_full) },
	{ 0xdb7305a1, __VMLINUX_SYMBOL_STR(__stack_chk_fail) },
	{ 0xe925b8b4, __VMLINUX_SYMBOL_STR(input_event) },
	{ 0x27e1a049, __VMLINUX_SYMBOL_STR(printk) },
	{ 0x20c55ae0, __VMLINUX_SYMBOL_STR(sscanf) },
	{ 0xbdfb6dbb, __VMLINUX_SYMBOL_STR(__fentry__) },
};

static const char __module_depends[]
__used
__attribute__((section(".modinfo"))) =
"depends=";


MODULE_INFO(srcversion, "FFD5583296C82D5654FFEE7");
