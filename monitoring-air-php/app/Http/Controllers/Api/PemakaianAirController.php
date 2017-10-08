<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Entities\PemakaianAir;
use Carbon\Carbon;
use Illuminate\Http\Request;

class PemakaianAirController extends Controller
{
    public function index(Request $request)
    {
        $pemakaianAir = PemakaianAir::select(
            \DB::raw('SUM(jumlah) AS jumlah'),
            \DB::raw('DATE(created_at) AS created_at')
        )
        ->where(function ($query) use($request) {
            $tanggal_dari = !empty($request->get('tanggal_dari')) ?
                Carbon::parse($request->get('tanggal_dari')) : date('Y-m-d');
            $tanggal_sampai = !empty($request->get('tanggal_sampai')) ?
                Carbon::parse($request->get('tanggal_sampai')) : date('Y-m-d');

            $query->whereDate('created_at', '>=', $tanggal_dari)
                ->whereDate('created_at', '<=', $tanggal_sampai);
        })
        ->groupBy(\DB::raw('DATE(created_at)'))
        ->get();

        $total = 0;

        $data = [
            'status' => 200,
            'total' => $total,
            'detail_pemakaian' => []
        ];

        foreach ($pemakaianAir as $row) {
            $data['detail_pemakaian'][] = [
                'jumlah' => $row->jumlah,
                'tanggal' => $row->created_at->format('d-m-Y'),
            ];
            $total += $row->jumlah;
        }

        if ($total > 0) {
            $data['total'] = $total;
        }

        return response()->json($data);
    }

    public function store(Request $request)
    {
        PemakaianAir::create(
            [
                'jumlah' => $request->get('jumlah')
            ]
        );

        return response()->json(['status' => 200, 'message' => 'Data berhasil disimpan']);
    }
}
