<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class ProductSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::table('products')->insert([
            'name' => "Iphone 13",
            'descripcion' => "Mobile Phone Apple",
            'amount' => 980
        ]);

        DB::table('products')->insert([
            'name' => "Ipad Pro 11",
            'descripcion' => "Tablet Apple",
            'amount' => 850
        ]);

        DB::table('products')->insert([
            'name' => "Playstation 5",
            'descripcion' => "Videoconsole",
            'amount' => 540
        ]);
    }
}