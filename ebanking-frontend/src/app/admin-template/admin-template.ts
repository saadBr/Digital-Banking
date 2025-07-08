import { Component } from '@angular/core';
import {Navbar} from '../navbar/navbar';

@Component({
  selector: 'app-admin-template',
  imports: [
    Navbar
  ],
  templateUrl: './admin-template.html',
  styleUrl: './admin-template.css'
})
export class AdminTemplate {

}
