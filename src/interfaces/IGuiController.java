package interfaces;

import data.ConcurrentResultMap;
import main.Settings;

public interface IGuiController extends IDuplicateSearchListener{
	void setResultMap(ConcurrentResultMap result);
}
