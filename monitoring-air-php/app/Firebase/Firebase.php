<?php

namespace App\Firebase;

class Firebase
{
    protected $firebase;

    public function __construct()
    {
        $this->firebase = new \Geckob\Firebase\Firebase(
            storage_path('app/google_credentials.json')
        );
    }

    public function setPath($path)
    {
        $this->firebase->setPath($path);
    }

    public function set($key, $value)
    {
        $this->firebase->set($key, $value);
    }

    public function get($key)
    {
        $result = $this->firebase->get($key);

        return $result;
    }
}
