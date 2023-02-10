import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DossierLoaderService {

  constructor(private http: HttpClient) {
  }

  public getProcessInstanceHistory(id: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/process/${id}/history`, {});
  }

  public getProcessInstanceTaskHistory(id: string): Observable<any[]> {
    return this.http.get<any[]>(`/api/history/${id}/tasks`, {});
  }

}
