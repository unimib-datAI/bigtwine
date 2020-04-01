import { IUser } from 'app/analysis/models';

export interface IDocument {
    id: string;
    filename: string;
    size: number;
    type?: string;
    analysisType?: string;
    analysisId?: string;
    category?: string;
    contentType?: string;
    uploadDate?: Date;
    user?: IUser;
}
