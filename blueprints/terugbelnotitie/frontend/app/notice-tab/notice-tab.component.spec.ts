import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeTabComponent } from './notice-tab.component';

describe('NoticeTabComponent', () => {
  let component: NoticeTabComponent;
  let fixture: ComponentFixture<NoticeTabComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NoticeTabComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
