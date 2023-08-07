package risyan.app.trustysnails.basecomponent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class BaseUseCase<PARAM, CONTENT_TYPE> : CoroutineScope{

    /** UseCase Original Response LiveData**/
    private val _currentData = MutableLiveData<Event<ResourceState<CONTENT_TYPE>>>()
    val currentData : LiveData<Event<ResourceState<CONTENT_TYPE>>> = _currentData

    /** Job Context Scope **/
    protected var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    /** Execute UseCase api or process **/
    fun execute(run : suspend () -> CONTENT_TYPE){
        launch(coroutineContext){
            val res : ResourceState<CONTENT_TYPE> = try {
                ResourceState.Success(run())
            } catch (e : Throwable){
                ResourceState.Failure(
                    e
                )
            }
            withContext(Dispatchers.Main){
                _currentData.postValue(Event(res))
            }
        }
    }

    /** Open function for user class to varies the execute call **/
    open fun setup(parameter: PARAM){ checkJob() }
    /** Common Job Control**/
    fun cancel() { job.cancel() }
    fun isCancelled(): Boolean { return job.isCancelled }
    fun checkJob(){
        if(job.isCancelled)
            job = Job() }
}