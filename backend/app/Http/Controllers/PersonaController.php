<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Persona;
use App\Models\PersonaUsuario;
use Illuminate\Support\Facades\Hash;

class PersonaController extends Controller
{
    // Crear una nueva persona y su usuario
    public function store(Request $request)
{
    try {
        $request->validate([
            'nombre' => 'required|string|max:255',
            'apellido' => 'required|string|max:255',
            'email' => 'required|email|unique:personas,email',
            'usuario' => 'required|string|unique:persona_usuario,usuario',
            'clave' => 'required|string|min:6',
            'rol' => 'required|in:Administrador,Estudiante,Estudiante Monitor' // Define los roles permitidos
        ]);

        // Iniciar transacción para asegurar consistencia en la BD
        \DB::beginTransaction();

        // Crear persona
        $persona = Persona::create([
            'nombre' => $request->nombre,
            'apellido' => $request->apellido,
            'email' => $request->email
        ]);

        // Si falla la creación de persona
        if (!$persona) {
            \DB::rollBack();
            return response()->json([
                'message' => 'Error al registrar la persona'
            ], 500);
        }

        // Crear usuario vinculado a la persona
        $usuario = PersonaUsuario::create([
            'persona_id' => $persona->id,
            'usuario' => $request->usuario,
            'clave' => Hash::make($request->clave), // Encriptar clave
            'rol' => $request->rol
        ]);

        // Si falla la creación del usuario
        if (!$usuario) {
            \DB::rollBack();
            return response()->json([
                'message' => 'Error al registrar el usuario'
            ], 500);
        }

        // Confirmar la transacción
        \DB::commit();

        return response()->json([
            'message' => 'Persona y usuario registrados correctamente'
        ], 201);
    } catch (\Illuminate\Validation\ValidationException $e) {
        return response()->json([
            'message' => 'Error de validación',
            'errors' => $e->errors()
        ], 422);
    } catch (\Exception $e) {
        \DB::rollBack();
        return response()->json([
            'message' => 'Error interno del servidor',
            'error' => $e->getMessage()
        ], 500);
    }
}

public function login(Request $request)
{
    try {
        $request->validate([
            'usuario' => 'required|string',
            'clave' => 'required|string'
        ]);

        // Buscar el usuario en la tabla persona_usuario
        $personaUsuario = PersonaUsuario::where('usuario', $request->usuario)->first();

        // Verificar si el usuario existe y si la clave es correcta
        if (!$personaUsuario || !Hash::check($request->clave, $personaUsuario->clave)) {
            return response()->json([
                'message' => 'Credenciales incorrectas'
            ], 401);
        }

        // Obtener los datos de la persona
        $persona = $personaUsuario->persona;

        return response()->json([
            'message' => 'Autenticación exitosa',
            'success' => true,
            'user' => [
                'nombre' => $persona->nombre,
                'apellido' => $persona->apellido,
                'email' => $persona->email,
                'rol' => $personaUsuario->rol
            ]
        ], 200);

    } catch (\Exception $e) {
        return response()->json([
            'message' => 'Error interno del servidor',
            'success' => true,
            'error' => $e->getMessage()
        ], 500);
    }
}


}
