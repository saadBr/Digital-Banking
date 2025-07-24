import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogViewer } from './log-viewer';

describe('LogViewer', () => {
  let component: LogViewer;
  let fixture: ComponentFixture<LogViewer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogViewer],
    }).compileComponents();

    fixture = TestBed.createComponent(LogViewer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
