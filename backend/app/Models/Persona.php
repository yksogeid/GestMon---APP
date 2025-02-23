<?php
namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Persona extends Model
{
    use HasFactory;

    protected $table = "personas"; // Tabla en la base de datos
    protected $fillable = ['nombre', 'apellido', 'email']; // Campos que se pueden asignar
}
