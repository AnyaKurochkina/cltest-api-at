# language: ru
@0003

Функциональность: Заказ продукта RHEL
  Предыстория: Получение токена
    * Получение Token под пользователем
      | username | portal_admin_at |
      | password | portal_admin_at |

  Сценарий: Заказ продукта RHEL
    * Заказ продукта RHEL в проекте proj-k7ua2iq6zh
      | count       | 1           |
      | net_segment | dev-srv-app |
      | platform    | OpenStack   |

    * Приостановить выполнение теста на 450 секунд
