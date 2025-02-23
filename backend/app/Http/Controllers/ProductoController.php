<?php

namespace App\Http\Controllers;

use App\Models\Producto;
use Illuminate\Http\Request;

class ProductoController extends Controller
{
    function getProducto(){
        $data = Producto::get();
       return response()->json($data);
    }
}
