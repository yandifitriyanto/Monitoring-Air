<?php

namespace App\Entities;

use Illuminate\Database\Eloquent\Model;

class StatusPompaAir extends Model
{
    protected $table = 'status_pompa_air';

    protected $fillable = [
        'status'
    ];
}
