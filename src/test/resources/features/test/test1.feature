# language: ru
@0002

Функционал: Тестовый пример

  Предыстория: Получение токена

    * Получение Token для пользователя
      | username | portal_admin_at |
      | password | portal_admin_at |

  Сценарий: Заказ продукта

    * Заказ продукта "Rhel" в проекте proj-0t3kfecbnf
      | count       | 1           |
      | net_segment | dev-srv-app |
      | platform    | Nutanix     |