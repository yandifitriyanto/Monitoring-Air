<?php

namespace App\Entities;

use Illuminate\Database\Eloquent\Model;

class PengisianAir extends Model
{
    protected $table = 'pengisian_air';

    protected $fillable = [
        'jumlah'
    ];
}
