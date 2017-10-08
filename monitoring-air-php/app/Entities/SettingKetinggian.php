<?php

namespace App\Entities;

use Illuminate\Database\Eloquent\Model;

class SettingKetinggian extends Model
{
    protected $table = 'setting_ketinggian';

    protected $fillable = [
        'maksimal', 'minimal'
    ];
}
