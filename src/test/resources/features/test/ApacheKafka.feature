# language: ru
@0007

Функционал: Тестовый пример

  Предыстория: Получение токена
    * Получение Token под пользователем
      | username | portal_admin_at |
      | password | portal_admin_at |

  Сценарий: Заказ продукта
    * Заказ продукта "ApacheKafka" в проекте proj-tg7jlitmp1
      | count       | 1           |
      | net_segment | dev-srv-app |
      | platform    | OpenStack   |