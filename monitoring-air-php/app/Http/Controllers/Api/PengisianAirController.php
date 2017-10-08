<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Entities\PengisianAir;
use App\Firebase\Firebase;
use Illuminate\Http\Request;

class PengisianAirController extends Controller
{
    protected $firebase;

    public function __construct(Firebase $firebase)
    {
        $this->firebase = $firebase;
    }

    public function index()
    {
        $pengisian_air = PengisianAir::first();

        $data = [
            'status' => 200,
            'jumlah' => $pengisian_air->jumlah ?? 0,
        ];

        return response()->json($data);
    }

    public function store(Request $request)
    {
        set_time_limit(0);

        $pengisian_air = PengisianAir::first();

        if ($pengisian_air) {
            $pengisian_air->jumlah = $request->get('jumlah');
            $pengisian_air->save();
        } else {
            $pengisian_air = PengisianAir::create(
                [
                    'jumlah' => $request->get('jumlah')
                ]
            );
        }

        if (config('app.realtime') == true) {
            $this->firebase->setPath('PengisianAir/');
            $this->firebase->set('jumlah', $pengisian_air->jumlah);
        }

        return response()->json(['status' => 200, 'message' => 'Data berhasil disimpan']);
    }
}
