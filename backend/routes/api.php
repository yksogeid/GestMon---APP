<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\PersonaController;

Route::prefix('v1')->group(function () {
Route::prefix('usuario')->group(function () {
        Route::post('/register', [PersonaController::class, 'store']);
        Route::post('/login', [PersonaController::class, 'login']);
    });
});
