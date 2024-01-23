####Для синхронизации битбакет с gitlab выполнить команду в папке с проектом:
`py -m sync_git_repos`

Перед этим:

1. Установить модуль `pip install sync-git-repos`
2. Добавить публичный ssh ключ в gitlab. Если нужно создать новый:

`eval 'ssh-agent -s'`

`ssh-keygen -t rsa -b 4096 -C "git"`

`ssh-add ~/.ssh/id_rsa`

Публичный ключ в`~/.ssh/id_rsa.pub` 