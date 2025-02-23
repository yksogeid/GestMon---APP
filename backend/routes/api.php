<?php

use App\Http\Controllers\ColorController;
use App\Http\Controllers\ProductoController;
use App\Http\Controllers\TallaController;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ProductController;
use App\Http\Controllers\PersonaController;
Route::post('register', [AuthController::class, 'register']);
Route::post('login', [AuthController::class, 'login']);


// ruta protegida por sanctum
Route::middleware(['auth:sanctum'])->group(function (){
    Route::get('logout', [AuthController::class, 'logout']);
  //  Route::prefix('talla')->group(function () {
      //  Route::get('/lista', [TallaController::class, 'getTalla']);
  //  });
});

Route::prefix('v1')->group(function () {
    Route::get('/students', function () {
        return "Logeado correctamente sapa";
    });
      Route::prefix('talla')->group(function () {
      Route::get('/lista', [TallaController::class, 'getTalla']);
  });
    Route::prefix('color')->group(function () {
        Route::get('/lista', [ColorController::class, 'getAutores']);
    });
    
    Route::prefix('producto')->group(function () {
        Route::get('/', [ProductoController::class, 'getProducto']);
    });

    Route::prefix('usuario')->group(function () {
        Route::post('/register', [PersonaController::class, 'store']);
        Route::post('/login', [PersonaController::class, 'login']);
    });
});
