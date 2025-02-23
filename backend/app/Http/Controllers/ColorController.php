<?php

namespace App\Http\Controllers;

use App\Models\Color;
use Illuminate\Http\Request;

class ColorController extends Controller
{
    function getAutores(){
        $data = Color::get();
       return response()->json($data);
    }
}
