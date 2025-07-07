import { Component, OnInit } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnInit{
  formLogin!: FormGroup;
  constructor(private fb : FormBuilder) {
  }

  ngOnInit(): void {
        this.formLogin=this.fb.group({
          username:this.fb.control(""),
          password:this.fb.control("")
        })
    }

  handleLogin() {
    console.log(this.formLogin.value);
  }
}
