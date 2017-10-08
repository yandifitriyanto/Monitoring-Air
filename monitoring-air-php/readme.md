## Requirement

1. PHP > v7.0
2. MySQL > v5.5


## Install, Config

1. `composer install`.
2. Buat file `.env` dengan meng-copy file `.env.example`.
3. Create MySQL database.
4. `php artisan key:generate`.
5. `php artisan migrate`.
6. `php artisan db:seed`.
7. `php artisan serve`
8. Seharusnya sudah bisa akses `http://localhost:8000`
