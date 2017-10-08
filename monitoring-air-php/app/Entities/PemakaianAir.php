<?php

namespace App\Entities;

use Illuminate\Database\Eloquent\Model;

class PemakaianAir extends Model
{
    protected $table = 'pemakaian_air';

    protected $fillable = [
        'jumlah'
    ];
}
