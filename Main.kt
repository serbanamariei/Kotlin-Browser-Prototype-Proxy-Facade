import khttp.responses.Response
import kotlin.collections.emptyMap

class GenericRequest(val url: String, val params: Map<String, String>?=null): Cloneable
{
    public override fun clone(): GenericRequest
    {
        return super.clone() as GenericRequest
    }
}

interface HTTPGet
{
    fun getResponse(): Response
}

class GetRequest(val timeout: Int, val genericReq: GenericRequest): HTTPGet
{
    override fun getResponse(): Response
    {
        val url: String = genericReq.url
        val params = genericReq.params ?: emptyMap()

        val response: Response = khttp.get(url = url, params = params, timeout = timeout.toDouble())
        return response
    }
}

class CleanGetRequest(val getReq: GetRequest, val parentalControlDisallow: List<String>): HTTPGet
{
    override fun getResponse(): Response
    {
        if(parentalControlDisallow.contains(getReq.genericReq.url))
        {
            throw Exception("acces interzis pentru site ul ${getReq.genericReq.url}")
        }

        return getReq.getResponse()
    }
}

class PostRequest(val genericReq: GenericRequest)
{
    fun postData(): Response
    {
        val url=genericReq.url
        val data = genericReq.params ?: emptyMap()

        val response: Response=khttp.post(url=url, data=data)
        return response
    }
}

class KidsBrowser(val cleanGet: CleanGetRequest, val postReq: PostRequest?)
{
    fun start()
    {
        try{
            val response=cleanGet.getResponse()
            println("Status pagina: ${response.statusCode}")
            println("Continut: ${response.text.take(200)}")
        }
        catch (e: Exception)
        {
            println("Eroare: ${e.message}")
        }
    }
}

fun main() {
    val siteuriInterzise = listOf("www.tiktok.com", "www.facebook.com")

    val model = GenericRequest("https://www.google.com")
    val browserOk = KidsBrowser(
        CleanGetRequest(GetRequest(5000, model), siteuriInterzise),
        PostRequest(model)
    )
    println("Test 1:")
    browserOk.start()

    println("\n")

    val modelRau = GenericRequest("www.tiktok.com")
    val browserBlocat = KidsBrowser(
        CleanGetRequest(GetRequest(5000, modelRau), siteuriInterzise),
        null
    )
    println("Test 2:")
    browserBlocat.start()
}