def status = it.status

blockquote('class':'twitter-tweet') {
    p status.text
    a href:"https://twitter.com/${status.user.screenName}/statuses/${status.id}"
}
script src:'//platform.twitter.com/widgets.js', charset:'UTF-8'