<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Persona;
use App\Models\PersonaUsuario;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\DB;

class PersonaController extends Controller
{
    public function store(Request $request)
    {
        try {
            $request->validate([
                'nombre' => 'required|string|max:255',
                'apellido' => 'required|string|max:255',
                'email' => 'required|email|unique:personas,email',
                'usuario' => 'required|string|unique:persona_usuario,usuario',
                'clave' => 'required|string|min:6'
            ]);

            DB::beginTransaction();

            $persona = Persona::create([
                'nombre' => $request->nombre,
                'apellido' => $request->apellido,
                'email' => $request->email
            ]);

            if (!$persona) {
                DB::rollBack();
                return $this->errorResponse('Error al registrar la persona', 500);
            }

            $usuario = PersonaUsuario::create([
                'persona_id' => $persona->id,
                'usuario' => $request->usuario,
                'clave' => Hash::make($request->clave),
                'rol' => 'Estudiante'
            ]);

            if (!$usuario) {
                DB::rollBack();
                return $this->errorResponse('Error al registrar el usuario', 500);
            }

            DB::commit();

            return $this->successResponse(['persona' => $persona, 'usuario' => $usuario], 'Persona y usuario registrados correctamente', 201);

        } catch (\Illuminate\Validation\ValidationException $e) {
            return $this->errorResponse('Error de validación', 422, $e->errors());
        } catch (\Exception $e) {
            DB::rollBack();
            return $this->errorResponse('Error interno del servidor', 500, ['exception' => $e->getMessage()]);
        }
    }

    public function login(Request $request)
    {
        try {
            $request->validate([
                'usuario' => 'required|string',
                'clave' => 'required|string'
            ]);

            $personaUsuario = PersonaUsuario::where('usuario', $request->usuario)->first();
// usuario: yksogeid
            if (!$personaUsuario || !Hash::check($request->clave, $personaUsuario->clave)) { // si el usuario es falso o si la clave no coincide
                return $this->errorResponse('Credenciales incorrectas', 401);
            }

            $persona = $personaUsuario->persona;

            return $this->successResponse([
                'nombre' => $persona->nombre,
                'apellido' => $persona->apellido,
                'email' => $persona->email,
                'rol' => $personaUsuario->rol
            ], 'Has iniciado sesion exitosamente.',200);

        } catch (\Exception $e) {
            return $this->errorResponse('Error interno del servidor', 500, ['exception' => $e->getMessage()]);
        }
    }
}
