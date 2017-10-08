<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

Route::group(['namespace' => 'Api'], function () {
    // setting ketinggian air
    Route::get(
        '/setting-ketinggian-air',
        'SettingKetinggianController@index'
    )->name('setting-ketinggian-air');

    Route::get(
        '/simpan-setting-ketinggian-air',
        'SettingKetinggianController@store'
    )->name('simpan-setting-ketinggian-air');

    // status pompa air
    Route::get(
        '/status-pompa-air',
        'StatusPompaAirController@index'
    )->name('status-pompa-air');

    Route::get(
        '/simpan-status-pompa-air',
        'StatusPompaAirController@store'
    )->name('simpan-status-pompa-air');

    // pengisian air
    Route::get(
        '/pengisian-air',
        'PengisianAirController@index'
    )->name('pengisian-air');

    Route::get(
        '/simpan-pengisian-air',
        'PengisianAirController@store'
    )->name('simpan-pengisian-air');

    // pemakaian air
    Route::get(
        '/pemakaian-air',
        'PemakaianAirController@index'
    )->name('pemakaian-air');

    Route::get(
        '/simpan-pemakaian-air',
        'PemakaianAirController@store'
    )->name('simpan-pemakaian-air');
});
