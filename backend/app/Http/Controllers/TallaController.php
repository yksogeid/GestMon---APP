<?php

namespace App\Http\Controllers;

use App\Models\Talla;
use Illuminate\Http\Request;

class TallaController extends Controller
{
    function getTalla(){
        $data = Talla::get();
       return response()->json($data);
    }
}
