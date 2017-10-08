<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Entities\StatusPompaAir;
use App\Firebase\Firebase;
use Illuminate\Http\Request;

class StatusPompaAirController extends Controller
{
    protected $firebase;

    public function __construct(Firebase $firebase)
    {
        $this->firebase = $firebase;
    }

    public function index()
    {
        $status_pompa_air = StatusPompaAir::first();

        $data = [
            'status' => 200,
            'status_pompa' => $status_pompa_air->status ?? 0,
        ];

        return response()->json($data);
    }

    public function store(Request $request)
    {
        set_time_limit(0);

        $status_pompa_air = StatusPompaAir::first();

        if ($status_pompa_air) {
            $status_pompa_air->status = $request->get('status');
            $status_pompa_air->save();
        } else {
            $status_pompa_air = StatusPompaAir::create(
                [
                    'status' => $request->get('status')
                ]
            );
        }

        if (config('app.realtime') == true) {
            $this->firebase->setPath('StatusPompa/');
            $this->firebase->set('status', $status_pompa_air->status);
        }

        return response()->json(['status' => 200, 'message' => 'Data berhasil disimpan']);
    }
}
