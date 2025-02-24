<?php

namespace App\Http\Controllers;

use Illuminate\Routing\Controller as BaseController;

class Controller extends BaseController
{
    protected function successResponse($data, $message = "Operación exitosa", $status = 200)
    {
        return response()->json([
            'success' => true,
            'message' => $message,
            'user' => $data
        ], $status);
    }

    protected function errorResponse($message, $status = 500, $errors = [])
    {
        return response()->json([
            'success' => false,
            'message' => $message,
            'errors' => $errors
        ], $status);
    }
}
