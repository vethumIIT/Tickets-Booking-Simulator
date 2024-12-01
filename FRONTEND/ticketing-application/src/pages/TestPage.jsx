function test(){
    var items = [];
    for(var i=1; i<=100; i++){
        items.push("item "+i);
    }

    return items;
}

const TestPage = () => {
    
    const items = test();

    return(
        <>
        <h1>This is a Test page</h1>
        <ul>
            {items.map((item, index) => (
                <li key={index}>{item}</li>
            ))}
        </ul>
        </>
    )

};

export default TestPage;