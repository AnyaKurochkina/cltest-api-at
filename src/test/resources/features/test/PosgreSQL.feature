# language: ru
@0005

Функционал: Заказ продукта PostgreSQL

  Предыстория: Получение токена
    * Получение Token


  Сценарий: Заказ продукта
    * Заказ продукта PostgreSQL в проекте proj-frk0ux52hp

    Если Статус заказа - success
    Тогда Выполнить действие - reset_vm

    Если Статус выполнения последнего действия - success
    Тогда Выполнить действие - stop_vm_soft

    Если Статус выполнения последнего действия - success
    Тогда Выполнить действие - start_vm

    Если Статус выполнения последнего действия - success
    Тогда Выполнить действие - stop_vm_hard

    Если Статус выполнения последнего действия - success
    Тогда Выполнить действие - delete_two_layer

    * Статус выполнения последнего действия - success