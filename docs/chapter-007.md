
## Chapter 007. Publisher & Subscriber Communication

* 다음과 같은 과정으로 코드 작성할 예정
    1. Publisher에 Subscriber 등록하기
    1. Publisher에서 onSubscribe 호출
    1. Subsriber에서 Subcription 객체를 통해 Publisher에게 데이터 전송하기
    1. Publisher에서 Subscription 객체로 onNext를 통해 데이터 전달하기
    1. Publisher에서 Subscription 객체로 onComplete을 통해 완료 알리기
    1. Publisher에서 문제 발생시 Subscription 객체로 onError를 통해 에러 전달하기
* 용어 정리
    * Publisher
        * Source
        * Observable
        * Upstream
        * Producer
    * Subsriber
        * Sink
        * Observer
        * Downstream
        * Consumer
