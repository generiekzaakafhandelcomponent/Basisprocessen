import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {DocumentService} from '@valtimo/document';

@Component({
  selector: 'app-notice-tab',
  templateUrl: './notice-tab.component.html',
  styleUrls: ['./notice-tab.component.scss']
})
export class NoticeTabComponent implements OnInit {
  public document;
  public documentId: string;
  private snapshot: ParamMap;

  constructor(private router: Router, private documentService: DocumentService, private route: ActivatedRoute) {
    this.snapshot = this.route.snapshot.paramMap;
    this.documentId = this.snapshot.get('documentId') || '';
  }

  ngOnInit(): void {
    this.init();
  }

  init() {
    this.documentService.getDocument(this.documentId).subscribe(document => {
      this.document = document;
    });
  }

}
