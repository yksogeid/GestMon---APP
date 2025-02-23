<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Database\Eloquent\Model;

class PersonaUsuario extends Authenticatable
{
    use HasFactory;

    protected $table = "persona_usuario";
    protected $fillable = ['persona_id', 'usuario', 'clave', 'rol'];

    // Relación con la persona
    public function persona()
    {
        return $this->belongsTo(Persona::class);
    }
}
