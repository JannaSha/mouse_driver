#include <linux/input.h>
#include <asm/uaccess.h>
#include <linux/fs.h>
#include <linux/pci.h>
#include <linux/platform_device.h>
#include <linux/module.h>

struct input_dev *mouse_input_dev; /* Представление устройства ввода */
static struct platform_device *mouse_dev; /* Структура устройства */

/* Метод для ввода из sysfs съэмулированных координат в драйвер виртуальной мыши */
static ssize_t write_mouse_coordinates(
    struct devie *dev, struct device_attribute *attr, 
    const char *buffer, size_t count)
{
    int x = 0, y = 0;
    sscanf(buffer, "%d%d", &x, &y);
    printk("AMA HERE");
    printk("%d %d", x, y);
    /* Сообщаем относительные координаты через интейрыейс событий */
    input_report_rel(mouse_input_dev, REL_X, x);
    input_report_rel(mouse_input_dev, REL_Y, y);
    input_sync(mouse_input_dev);
    return count;
}

/* Подключаем метод записи sysfs */
DEVICE_ATTR(coordinates, 0644, NULL, write_mouse_coordinates);

/* Десктриптор атрибутов */
static struct attribute *vms_attrs[] = {
    &dev_attr_coordinates.attr, NULL
};

/* Группа атрибутов */
static struct attribute_group vms_attr_group = {
    .attrs = vms_attrs,
};

/* Инициализация драйвера */
static int __init mouse_init(void)
{
    /* Регестрируем устройсво платформы */
    mouse_dev = platform_device_register_simple("my_mouse", -1, NULL, 0);
    if (IS_ERR(mouse_dev)) {
        printk("mouse_init: error\n");
        return PTR_ERR(mouse_dev);
    }

    /* Создаем в sysfs узел для чтения съэмулированных координат */
    sysfs_create_group(&mouse_dev->dev.kobj, &vms_attr_group);

    /* Создаем структуру данных устройства ввода */
    mouse_input_dev = input_allocate_device();
    if (!mouse_input_dev) {
        printk("Bad input_allocate_device()\n");
        return -ENOMEM;
    }

    /* Сообщаем, что эта виртуальная мышь будеи генерировать относителные координаты */
    /* Движение */
    set_bit(EV_REL, mouse_input_dev->evbit);
    set_bit(REL_X, mouse_input_dev->relbit);
    set_bit(REL_Y, mouse_input_dev->relbit);
    /* Нажатие */
    set_bit(EV_KEY, mouse_input_dev->evbit);
    set_bit(BTN_0, mouse_input_dev->keybit);

    /* Регистрируемся в подсистеме ввода */
    input_register_device(mouse_input_dev);

    printk("Virtual Mouse Driver Initialized \n");
    return 0;
}

/* Выход из драйвера */
static void mouse_cleanup(void)
{
    /* Отменяем регистрацию в подсистеме ввода */
    input_unregister_device(mouse_input_dev);

    /* Освобождаем узел в sysfs */
    sysfs_remove_group(&mouse_dev->dev.kobj, &vms_attr_group);

    /* Отменяем регистрацию драйвера */
    platform_device_unregister(mouse_dev);
}

module_init(mouse_init);
module_exit(mouse_cleanup);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Shapoval Janna Inc.");