            // ⭐ شاشەی پریڤیو: بچووکتر کرا و کەمێک برا بۆ لای ڕاست
            if (logoResId != null) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Channel Logo",
                    modifier = Modifier
                        .align(AbsoluteAlignment.TopRight)
                        .absolutePadding(top = 5.dp, right = 15.dp) // ژمارەکە کەمکرایەوە تا بچێتە ڕاست
                        .width(65.dp) 
                        .height(26.dp)
                )
            }
