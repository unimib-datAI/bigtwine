import { IAnalysisResult } from 'app/analysis/models/analysis-result.model';
import { INeelProcessedTweet } from 'app/analysis/twitter-neel';

export interface ITwitterNeelAnalysisResult extends IAnalysisResult{
    payload: INeelProcessedTweet;
}
