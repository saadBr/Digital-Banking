import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { UserService } from '../services/user.service';
import { CommonModule } from '@angular/common';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-new-user',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './new-user.html',
  styleUrl: './new-user.css',
})
export class NewUser {
  userForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private toast: ToastService,
  ) {
    this.userForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      roles: this.fb.control('USER', Validators.required),
    });
  }

  createUser() {
    if (this.userForm.invalid) {
      this.toast.showError('Please fill in all required fields correctly.');
      return;
    }

    const rawValue = this.userForm.value;
    const selectedRole = rawValue.roles;

    const roles = selectedRole === 'ADMIN' ? ['ADMIN', 'USER'] : ['USER'];

    const payload = {
      username: rawValue.username,
      password: rawValue.password,
      email: rawValue.email,
      roles: roles,
    };

    this.userService.createUser(payload).subscribe({
      next: () => {
        this.toast.showSuccess('User created successfully!');
        this.userForm.reset({ roles: 'USER' });
      },
      error: (err) => {
        console.error(err);
        this.toast.showError(err?.error || 'Failed to create user.');
      },
    });
  }
}
