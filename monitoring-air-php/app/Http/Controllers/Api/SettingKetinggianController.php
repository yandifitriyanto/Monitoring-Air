<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Entities\SettingKetinggian;
use Illuminate\Http\Request;

class SettingKetinggianController extends Controller
{
    public function index()
    {
        $setting_ketinggian = SettingKetinggian::first();

        $data = [
            'status' => 200,
            'maksimal' => $setting_ketinggian->maksimal ?? 0,
            'minimal' => $setting_ketinggian->minimal ?? 0,
        ];

        return response()->json($data);
    }

    public function store(Request $request)
    {
        $setting_ketinggian = SettingKetinggian::first();

        if ($setting_ketinggian) {
            $setting_ketinggian->maksimal = $request->get('maksimal');
            $setting_ketinggian->minimal = $request->get('minimal');
            $setting_ketinggian->save();
        } else {
            $setting_ketinggian = SettingKetinggian::create(
                [
                    'maksimal' => $request->get('maksimal'),
                    'minimal' => $request->get('minimal'),
                ]
            );
        }

        return response()->json(['status' => 200, 'message' => 'Data berhasil disimpan']);
    }

}
